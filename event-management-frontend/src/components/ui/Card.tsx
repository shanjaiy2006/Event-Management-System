import React from 'react';
import { cn } from '@/utils/cn';

export interface CardProps extends React.HTMLAttributes<HTMLDivElement> {
 hoverEffect?: boolean;
}

export const Card: React.FC<CardProps> = ({
 className,
 children,
 hoverEffect = false,
 ...props
}) => {
 return (
 <div
 className={cn(
 "bg-card border-border shadow-md rounded-2xl p-6 overflow-hidden transition-all duration-300",
 hoverEffect && "hover:-translate-y-1 hover:shadow-lg hover:border-primary/30 hover:scale-[1.01]",
 className
 )}
 {...props}
 >
 {children}
 </div>
 );
};
